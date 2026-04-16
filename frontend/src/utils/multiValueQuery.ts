/** 与后端 MultiValueTextParse.MAX_VALUES_PER_FIELD 一致 */
export const MULTI_VALUE_MAX = 100

function stripBom(s: string): string {
  let t = s
  while (t.length > 0 && t.charCodeAt(0) === 0xfeff) {
    t = t.slice(1)
  }
  return t
}

/** 与后端 MultiValueTextParse一致：零宽字符、Unicode 连字符 → ASCII「-」 */
function normalizePastedToken(raw: string): string {
  const t = stripBom(raw).trim()
  if (!t) return ''
  let out = ''
  for (let i = 0; i < t.length; i++) {
    const c = t.charCodeAt(i)
    if (c === 0xfeff || (c >= 0x200b && c <= 0x200d)) continue
    if (c === 0x2010 || c === 0x2011 || c === 0x2012 || c === 0x2013 || c === 0x2014 || c === 0x2212 || c === 0x00ad) {
      out += '-'
    } else {
      out += t[i]
    }
  }
  return out.trim()
}

/** 换行/逗号/分号/Tab/空白分隔；去 BOM（避免从 CSV/Excel 粘贴首条查不到）；去重（保留顺序） */
export function parseMultiValueLines(raw: string): string[] {
  const normalized = stripBom(raw ?? '').trim()
  if (!normalized) return []
  const seen = new Set<string>()
  const out: string[] = []
  const push = (v: string) => {
    const t = normalizePastedToken(v)
    if (!t || seen.has(t)) return
    seen.add(t)
    out.push(t)
  }
  const splitOneLine = (row: string) => {
    const r = stripBom(row).trim()
    if (!r) return
    if (/[,，;；]/.test(r)) {
      for (const p of r.split(/[,，;；]+/)) push(p)
      return
    }
    if (r.includes('\t')) {
      const parts = r.split(/\t+/).filter((p) => p.trim())
      if (parts.length > 1) {
        for (const p of parts) push(p)
        return
      }
    }
    const sp = r.split(/\s+/).filter(Boolean)
    if (sp.length > 1) {
      for (const p of sp) push(p)
      return
    }
    push(r)
  }
  for (const line of normalized.split(/\r?\n/)) {
    splitOneLine(line)
  }
  return out
}

/** @deprecated 使用 {@link parseMultiValueLines} */
export const parseSpaceSeparatedValues = parseMultiValueLines

/** 任一字段超过上限则返回错误文案，否则 null */
export function validateMultiValueInput(fields: Record<string, string>): string | null {
  for (const [label, raw] of Object.entries(fields)) {
    if (!raw?.trim()) continue
    const n = parseMultiValueLines(raw).length
    if (n > MULTI_VALUE_MAX) {
      return `「${label}」最多输入 ${MULTI_VALUE_MAX} 个值（请换行输入，每行一条）`
    }
  }
  return null
}

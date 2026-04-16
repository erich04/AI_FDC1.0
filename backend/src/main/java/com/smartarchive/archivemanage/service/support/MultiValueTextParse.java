package com.smartarchive.archivemanage.service.support;

import com.smartarchive.common.exception.BusinessException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.util.StringUtils;

/** 筛选栏多值：换行/逗号/分号/空白分隔（与前端一致），空行忽略，单字段最多条数上限。 */
public final class MultiValueTextParse {
    public static final int MAX_VALUES_PER_FIELD = 100;

    private MultiValueTextParse() {
    }

    /**
     * 解析多值：先按换行切行；每行再按逗号/分号（含全角）或空白拆成多条；每段去 BOM、trim。
     * 多条时查询侧通常按「精确匹配」比对，故需去掉 Excel/CSV 粘贴带来的首行 BOM。
     *
     * @throws BusinessException 超过 {@link #MAX_VALUES_PER_FIELD} 条
     */
    public static List<String> parseSpaceSeparatedValues(String raw) {
        if (!StringUtils.hasText(raw)) {
            return List.of();
        }
        String normalized = stripBom(raw).trim();
        if (!StringUtils.hasText(normalized)) {
            return List.of();
        }
        Set<String> seen = new LinkedHashSet<>();
        for (String line : normalized.split("\\R")) {
            String row = stripBom(line).trim();
            if (!StringUtils.hasText(row)) {
                continue;
            }
            for (String token : splitLineIntoTokens(row)) {
                String t = normalizePastedToken(token);
                if (StringUtils.hasText(t)) {
                    seen.add(t);
                }
            }
        }
        if (seen.size() > MAX_VALUES_PER_FIELD) {
            throw new BusinessException("单个筛选条件最多支持 " + MAX_VALUES_PER_FIELD + " 个值（请换行输入，每行一条）");
        }
        return new ArrayList<>(seen);
    }

    private static String stripBom(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        int i = 0;
        while (i < s.length() && s.charAt(i) == '\uFEFF') {
            i++;
        }
        return i == 0 ? s : s.substring(i);
    }

    /**
     * 粘贴自 Excel/网页时常见：零宽字符、Unicode 连字符与库内 ASCII「-」不一致，导致多条精确匹配全军覆没。
     */
    /** 供 Controller 在收到 {@code businessCodes} 数组时对每项做与多行解析一致的规范化。 */
    public static String normalizeToken(String raw) {
        return normalizePastedToken(raw);
    }

    private static String normalizePastedToken(String raw) {
        if (raw == null) {
            return "";
        }
        String t = stripBom(raw).trim();
        if (t.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(t.length());
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (c == '\uFEFF' || (c >= '\u200B' && c <= '\u200D')) {
                continue;
            }
            if (c == '\u2010' || c == '\u2011' || c == '\u2012' || c == '\u2013' || c == '\u2014' || c == '\u2212' || c == '\u00AD') {
                sb.append('-');
            } else {
                sb.append(c);
            }
        }
        return sb.toString().trim();
    }

    /** 单行内：优先按标点拆；否则多段空白视为多条编码。 */
    private static List<String> splitLineIntoTokens(String row) {
        if (row.contains(",") || row.contains("，") || row.contains(";") || row.contains("；")) {
            String[] parts = row.split("[,，;；]+");
            List<String> out = new ArrayList<>();
            for (String p : parts) {
                String t = p.trim();
                if (StringUtils.hasText(t)) {
                    out.add(t);
                }
            }
            return out.isEmpty() ? List.of(row) : out;
        }
        if (row.contains("\t")) {
            String[] parts = row.split("\\t+");
            List<String> out = new ArrayList<>();
            for (String p : parts) {
                String t = p.trim();
                if (StringUtils.hasText(t)) {
                    out.add(t);
                }
            }
            return out.size() <= 1 ? List.of(row) : out;
        }
        String[] sp = row.split("\\s+");
        if (sp.length > 1) {
            return Arrays.asList(sp);
        }
        return List.of(row);
    }
}

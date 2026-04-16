import http, { apiRequest } from '../http'
import type { CountryRegionItem } from '../../types'

export interface CountryRegionQuery {
  regionLevel: 'COUNTRY' | 'PROVINCE' | 'CITY'
  parentRegionCode?: string
}

export function fetchCountryRegions(params: CountryRegionQuery) {
  return apiRequest<CountryRegionItem[]>(http.get('/api/base-data/country-regions', { params }))
}

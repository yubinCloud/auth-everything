package org.inet.aet.uappmaker.feign.client

import org.springframework.cloud.openfeign.FeignClient

@FeignClient("avue-data-server")
interface AvueDataServerClient {
}
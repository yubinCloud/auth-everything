package com.example.eusergateway.service;

import com.example.eusergateway.exchange.response.AvuePermission;
import com.example.eusergateway.exchange.response.PublicApiPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionFactory permissionFactory;

    public Set<String> convertAvuePermissions(List<AvuePermission> permissionList) {
        return permissionList.stream().flatMap(this::convertOneAuePermission).collect(Collectors.toSet());
    }

    public Set<String> convertPublicApiPermissions(List<PublicApiPermission> publicApiPermissions) {
        return publicApiPermissions.stream().map(PublicApiPermission::getApiId).map(permissionFactory::createPublicApiIdPermission).collect(Collectors.toSet());
    }

    private Stream<String> convertOneAuePermission(AvuePermission ap) {
        List<String> permissions = new ArrayList<>();
        permissions.add(permissionFactory.createVisualIdPermission(ap.getVisualId()));
        permissions.addAll(ap.getWhitelist().stream().map(permissionFactory::createVisualComponentPermission).toList());
        return permissions.stream();
    }


}

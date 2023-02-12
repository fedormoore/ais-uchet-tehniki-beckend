package ru.moore.AISUchetTehniki.multi_tenancy;

import java.util.UUID;

public class TenantContext {

    private TenantContext() {}

    private static ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    public static void setTenantId(UUID tenantId) {
        currentTenant.set(tenantId);
    }

    public static UUID getTenantId() {
        return currentTenant.get();
    }

    public static void clear(){
        currentTenant.remove();
    }

}

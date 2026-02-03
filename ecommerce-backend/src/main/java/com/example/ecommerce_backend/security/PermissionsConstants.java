package com.example.ecommerce_backend.security;

import org.springframework.stereotype.Component;

@Component("permissionsConstants")
public class PermissionsConstants {

    public static final String CATEGORY_VIEW = "CATEGORY_VIEW";
    public static final String CATEGORY_CREATE = "CATEGORY_CREATE";
    public static final String CATEGORY_UPDATE = "CATEGORY_UPDATE";
    public static final String CATEGORY_DELETE = "CATEGORY_DELETE";


    // =======================
    // PRODUCT - ADMIN
    // =======================
    public static final String PRODUCT_VIEW = "PRODUCT_VIEW";
    public static final String PRODUCT_CREATE = "PRODUCT_CREATE";
    public static final String PRODUCT_DELETE = "PRODUCT_DELETE";
    public static final String PRODUCT_UPDATE = "PRODUCT_UPDATE";

}
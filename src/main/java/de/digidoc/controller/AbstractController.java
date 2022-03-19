package de.digidoc.controller;

import org.springframework.ui.Model;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractController {

    private Map<String, String> breadcrumbs = new LinkedHashMap<>();

    protected void showBreadcrumbs(Model model) {
        model.addAttribute("breadcrumbs", breadcrumbs);
    }

    public Map<String, String> getBreadcrumbs() {
        return getBreadcrumbs(false);
    }

    public Map<String, String> getBreadcrumbs(boolean clear) {
        if (clear) {
            breadcrumbs = new LinkedHashMap<>();
        }
        return breadcrumbs;
    }
}

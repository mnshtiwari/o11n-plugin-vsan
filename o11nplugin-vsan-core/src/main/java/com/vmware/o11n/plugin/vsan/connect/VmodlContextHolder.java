package com.vmware.o11n.plugin.vsan.connect;

import com.vmware.vim.vmomi.core.types.VmodlContext;

public interface VmodlContextHolder {
    //String REFLECT_PACKAGE = "com.vmware.vim.binding.vmodl.reflect";
    String VIM_PACKAGE = "com.vmware.vim.binding.vim";
    String VSAN_PACKAGE = "com.vmware.vim.vsan.binding.vim";
    String[] VMODL_PACKAGES = new String[] {
            VIM_PACKAGE,
            VSAN_PACKAGE
    };

    /**
     * Returns a singleton VmodlContext value , held by this holder
     *
     * @return
     */
    VmodlContext getValue();
}

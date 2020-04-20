package com.vmware.o11n.plugin.vsan;

import java.lang.reflect.Method;

import com.vmware.o11n.sdk.modeldrivengen.mapping.MethodRenamePolicy;
import com.vmware.vim.binding.vmodl.wsdlName;

final class TaskMethodRenamer implements MethodRenamePolicy {
   @Override
   public String rename(Method method) {
      String methodName = null;
      if(method.getName().equals("getContext")) {
         methodName = "getAdditionalContext";
      } else if(method.getName().equals("setContext")) {
         methodName = "setAdditionalContext";
      } else {
         wsdlName nameAnnotation = method.getAnnotation(wsdlName.class);
         if (nameAnnotation != null) {
            methodName = nameAnnotation.value();
         }
      }
      if (methodName != null && methodName.length() > 0) {
         return rename(methodName);
      }

      return method.getName();
   }

   private String rename(String methodName) {
      StringBuilder sb = new StringBuilder(methodName);

      sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
      return sb.toString();
   }
}

package com.vmware.o11n.plugin.vsan;

import java.util.HashSet;
import java.util.Set;

import com.vmware.o11n.plugin.vsan.finder.VsanConnectionFinder;
import com.vmware.o11n.plugin.vsan.model.ConnectionInfo;
import com.vmware.o11n.plugin.vsan.model.VsanConnection;
import com.vmware.o11n.plugin.vsan.relator.RootHasConnections;
import com.vmware.o11n.plugin.vsan.singleton.ConnectionManager;
import com.vmware.o11n.sdk.modeldriven.ObjectFinder;
import com.vmware.o11n.sdk.modeldrivengen.mapping.AbstractMapping;
import com.vmware.o11n.sdk.modeldrivengen.mapping.MethodRenamePolicy;
import com.vmware.o11n.sdk.modeldrivengen.mapping.WrapDescriptor;
import com.vmware.o11n.sdk.modeldrivengen.mapping.WrapperCustomizer;
import com.vmware.vim.binding.vmodl.ManagedObjectReference;
import com.vmware.vim.binding.vmodl.TypeName;
import com.vmware.vim.vmomi.core.types.VmodlContext;
import com.vmware.vim.vmomi.core.types.VmodlType;
import com.vmware.vim.vmomi.core.types.VmodlType.Kind;
import com.vmware.vim.vmomi.core.types.VmodlTypeMap;

public class CustomMapping extends AbstractMapping {
   private final MethodRenamePolicy renamer = new TaskMethodRenamer();

    @SuppressWarnings("unchecked")
    @Override
    public void define() {
    	
    	//convertWellKnownTypes();
    	
    	singleton(ConnectionManager.class).as("VsanConnectionManager");;
    	
    	wrap(VsanConnection.class).
         andFind().
         using(VsanConnectionFinder.class).
         withIcon("connection.png");
    	
    	String vimPackage = "com.vmware.vim.binding.vim";
    	String vsanPackage = "com.vmware.vim.vsan.binding.vim";
    	String[] vmodlContextPackage = new String[] {vimPackage, vsanPackage};
    	VmodlContext vmodlContext = initVmodlContext(vmodlContextPackage);

      VmodlTypeMap vmodlTypeMap = vmodlContext.getVmodlTypeMap();
      Set<VmodlType> uniqueTypes = new HashSet<>();
      uniqueTypes.addAll(vmodlTypeMap.getVmodlTypes());

      WrapperCustomizer customizer = new WrapperCustomizer(this);

      Set<Class<?>> excludeClasses = new HashSet<>();
      for (VmodlType type : uniqueTypes) {
          String wsdlName = type.getWsdlName();

          Class<?> clazz = loadClass(type.getTypeName());
          
          // Exclude wrapping VIM vmodl types
          if (clazz.getName().contains(vimPackage)) {
             continue;
          }
          if (!excludeClasses.contains(clazz)) {

              String sdkName= wsdlName;
              if (type.getKind() == Kind.MANAGED_OBJECT
                    || type.getKind() == Kind.DATA_OBJECT
                    || type.getKind() == Kind.FAULT) {

                  WrapDescriptor<?> wrapper;
                  wrapper = wrap(clazz).as(sdkName).rename(renamer);
                  if (type.getKind() == Kind.MANAGED_OBJECT) {
                  } else {
                      wrapper.propagateRootId();
                      if (type.getKind() == Kind.FAULT) {
                          wrapper.hiding("printStackTrace");
                      }
                  }
                  // reflectively customize the wrapping
                  customizer.tryCustomize(wrapper, clazz);
              } else if (type.getKind() == Kind.ENUM) {
                  //enumerate((Class<Enum>) clazz).as(sdkName);
              }

          }
      }

      include(ManagedObjectReference.class);
    	
    	//wrap(ManagedObjectReference.class);
    	//wrap(VsanVcClusterConfigSystem.class);
    	//wrap(VsanClusterConfig.class);
    	
    	relateRoot().
         to(VsanConnection.class).
         using(RootHasConnections.class).
         as("connections");
    }
    
   public static Class loadClass(TypeName typeName) {
      try {
         return Class.forName(typeName.getName());
      } catch (ClassNotFoundException e) {
         throw new AssertionError(e);
      }
   }
   
   private synchronized static VmodlContext initVmodlContext(String[] context) {
      try {
         return VmodlContext.getContext();
      } catch (IllegalStateException ex) {
         return VmodlContext.initContext(context);
      }
   }
}
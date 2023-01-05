package com.nconnect.usersupport.infrastructure;


import com.nconnect.usersupport.infrastructure.resource.CorsNegotiation;
import io.vlingo.xoom.http.CORSResponseFilter;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.SinglePageApplicationConfiguration;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.xoom.turbo.XoomInitializationAware;
import io.vlingo.xoom.turbo.annotation.initializer.ResourceHandlers;
import io.vlingo.xoom.turbo.annotation.initializer.Xoom;

import java.util.Arrays;
import java.util.List;

// TODO: Replace auto generated initializer code by own code to set CORS filters on the server instance
@Xoom(name = "usersupport")
@ResourceHandlers(packages = "com.nconnect.usersupport.infrastructure.resource")
public class Bootstrap implements XoomInitializationAware {

  @Override
  public void onInit(final Grid grid) {

  }

  @Override
  public SinglePageApplicationConfiguration singlePageApplicationResource() {
    return SinglePageApplicationConfiguration.defineWith("/frontend", "/app");
  }

}

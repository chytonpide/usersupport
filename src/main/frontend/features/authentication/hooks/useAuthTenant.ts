import {useEffect, useState} from "react";

import {getAuthTenant} from "../auth";
import {Tenant} from "../data/Tenant";


export const useAuthTenant = () => {
  const [authTenant, setAuthTenant] = useState<Tenant | null>(  null);

  useEffect(() => {
    setAuthTenant(getAuthTenant)
  },[JSON.stringify(authTenant)])

  return authTenant
};

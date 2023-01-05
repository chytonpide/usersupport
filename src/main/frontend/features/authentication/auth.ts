import cookies from "js-cookie";
import {AuthStatus} from "./authStatus";
import {PATH_TO_REQUIRED_AUTH} from "../../configs/pathToRequiredAuth";
import {useEffect, useState} from "react";
import {User} from "./data/User";
import {Tenant} from "./data/Tenant";

const COOKIE_AUTH_TENANT_KEY = "usersupport-auth-tenant"
const COOKIE_USERS_KEY = "usersupport-users"
const COOKIE_AUTH_USER_KEY = "usersupport-auth-user"


export const signInUser = (user: User) => {
  cookies.set(COOKIE_AUTH_USER_KEY, JSON.stringify(user));
};

export const signOutUser = () => {
  cookies.remove(COOKIE_AUTH_USER_KEY);
};

export const signOutTenant = () => {
  cookies.remove(COOKIE_AUTH_TENANT_KEY);
  cookies.remove(COOKIE_AUTH_USER_KEY);
  cookies.remove(COOKIE_USERS_KEY);
};

export const signUp = (tenant: Tenant, users: User[]) => {
  cookies.set(COOKIE_AUTH_TENANT_KEY, JSON.stringify(tenant));
  cookies.set(COOKIE_USERS_KEY, JSON.stringify(users));
};

export const userToken = (user: User) => {
  return `usersupport id=${user.id}, name=${user.name}`;
}

export const auth = (path: string) => {

  const authStatus = getAuthStatus()
  const requiredAuthStatus = getRequiredAuthOfPath(path)

  if (
    authStatus == AuthStatus.NONE &&
    (requiredAuthStatus == AuthStatus.TENANT_AUTHENTICATED ||
      requiredAuthStatus == AuthStatus.TENANT_USER_AUTHENTICATED)
  ) {
    return {
      authenticated: false,
      redirectPath:"/tenant"
    }
  }

  if (
    authStatus == AuthStatus.TENANT_AUTHENTICATED &&
    requiredAuthStatus == AuthStatus.TENANT_USER_AUTHENTICATED
  ) {
    return {
      authenticated: false,
      redirectPath:"/users"
    }
  }

  return {
    authenticated: true,
    redirectPath:""
  }

};

const getAuthStatus = () => {
  const rawAuthTenant = cookies.get(COOKIE_AUTH_TENANT_KEY);
  const rawAuthUser = cookies.get(COOKIE_AUTH_USER_KEY);

  if (rawAuthTenant && !rawAuthUser) {
    return AuthStatus.TENANT_AUTHENTICATED;
  } else if (rawAuthTenant && rawAuthUser) {
    return AuthStatus.TENANT_USER_AUTHENTICATED;
  } else {
    return AuthStatus.NONE;
  }
}

export const getRequiredAuthOfPath = (aPath: string) => {
  let path = aPath;

  const wildCardPaths = Object.keys(PATH_TO_REQUIRED_AUTH).filter((path) => {
    return path.endsWith("*");
  });

  wildCardPaths.forEach((wildCardPath) => {
    if (path.startsWith(wildCardPath.slice(0, -1))) {
      path = wildCardPath;
    }
  });

  const typedKey = path as keyof typeof PATH_TO_REQUIRED_AUTH;
  const result = PATH_TO_REQUIRED_AUTH[typedKey];

  return result;
};


export const getAuthUser = () => {
  const rawAuthUser = cookies.get(COOKIE_AUTH_USER_KEY);
  if(rawAuthUser) {
    const authUser = JSON.parse(rawAuthUser) as User;
    return authUser
  } else {
    return null
  }
}

export const getAuthTenant = () => {
  const rawAuthTenant = cookies.get(COOKIE_AUTH_TENANT_KEY);
  if(rawAuthTenant) {
    const authTenant = JSON.parse(rawAuthTenant) as User;
    return authTenant
  } else {
    return null
  }
}

export const getUsers = () => {
  const rawUsers = cookies.get(COOKIE_USERS_KEY);
  if(rawUsers) {
    const users = JSON.parse(rawUsers) as User[];
    return users
  } else {
    return null
  }
}

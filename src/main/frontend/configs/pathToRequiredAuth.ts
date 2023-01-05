import {AuthStatus} from "../features/authentication/authStatus";


export const PATH_TO_REQUIRED_AUTH = {
  "/support-cases": AuthStatus.TENANT_USER_AUTHENTICATED,
  "/support-cases/*": AuthStatus.TENANT_USER_AUTHENTICATED,
  "/tenant": AuthStatus.NONE,
  "/users": AuthStatus.TENANT_AUTHENTICATED,
}
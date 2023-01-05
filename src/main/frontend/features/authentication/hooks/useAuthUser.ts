import { useEffect, useState } from "react";
import {getAuthUser} from "../auth";
import {User} from "../data/User";

export const useAuthUser = () => {
  const [authUser, setAuthUser] = useState<User | null>(
    null
  );

  useEffect(() => {
    setAuthUser(getAuthUser);
  }, [JSON.stringify(authUser)]);

  return authUser;
};

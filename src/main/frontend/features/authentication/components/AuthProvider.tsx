import React, { useEffect } from "react";
import { useRouter } from "next/router";

import {auth} from "../auth";

type Props = {
  children: React.ReactNode;
};

const AuthProvider: React.FC<Props> = ({ children }) => {
  const router = useRouter();
  const { asPath } = useRouter();
  const result = auth(asPath);

  useEffect(() => {
    if (!result.authenticated) {
      router.push(result.redirectPath);
    }
  });

  return <div>{children}</div>;

};
export default AuthProvider;

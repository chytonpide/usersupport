import PageMeta from "../features/common/components/PageMeta";
import { useEffect, useState } from "react";
import { useRouter } from "next/router";
import { AuthStatus } from "../features/authentication/authStatus";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    router.push("/support-cases");
  },[]);

  return (<></>
  );
}

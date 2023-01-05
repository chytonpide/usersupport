import React from "react";
import { LayoutName } from "./layoutName";
import PlainLayout from "./PlainLayout";
import DefaultLayout from "./DefaultLayout";
import {pathToLayoutName} from "./layoutMapper";
import {useRouter} from "next/router";

type Props = {
  children: React.ReactNode;
};

const LayoutProvider: React.FC<Props> = ({ children }) => {
  const { asPath } = useRouter();
  const layoutName = pathToLayoutName(asPath);

  switch (layoutName) {
    case LayoutName.PLAIN:
      return <PlainLayout>{children}</PlainLayout>;
    default:
      return <DefaultLayout>{children}</DefaultLayout>;
  }
};
export default LayoutProvider;

import { LayoutProps } from "next/dist/lib/app-layout";
import NavBar from "../common/components/NavBar";
import PageMeta from "../common/components/PageMeta";

export default function DefaultLayout(
  props: React.PropsWithChildren<LayoutProps>
) {
  return (
    <div>
      <NavBar />
      <div>{props.children}</div>
    </div>
  );
}

import {LayoutProps} from "next/dist/lib/app-layout";
import NavBar from "../common/components/NavBar";

export default function PlainLayout(props: React.PropsWithChildren<LayoutProps>) {
    return (
      <div className="container">
        <div className="row">
          <div className="col-lg-2"></div>
          <div className="col-lg-8">{props.children}</div>
          <div className="col-lg-2"></div>
        </div>
      </div>
    )
}
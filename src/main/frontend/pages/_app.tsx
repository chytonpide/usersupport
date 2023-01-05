import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "../styles/globals.css";
import type { AppProps } from "next/app";
import { useEffect } from "react";
import AuthProvider from "../features/authentication/components/AuthProvider";
import LayoutProvider from "../features/layout/LayoutProvider";

function MyApp({ Component, pageProps }: AppProps) {


  useEffect(() => {
    typeof document !== undefined
      ? require("bootstrap/dist/js/bootstrap")
      : null;
  }, []);


  return (
    <div className="wrapper text-light bg-dark">
      <AuthProvider>
        <LayoutProvider>
          <Component {...pageProps} />
        </LayoutProvider>
      </AuthProvider>

      <style jsx global>{`
        a {
          color: orange;
        }
      `}</style>
      <style jsx>{`
        .wrapper {
          min-height: calc(100vh);
          width: 100%;
        }
      `}</style>
    </div>
  );
}

export default MyApp;

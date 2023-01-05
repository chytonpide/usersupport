import PageMeta from "../../features/common/components/PageMeta";
import SupportCaseOpenBox from "../../features/supportcase/components/SupportCaseOpenBox";
import React from "react";
import {useRouter} from "next/router";

export const getStaticProps = async () => ({
  props: {
    authRequired: true,
  },
});

export default function AddPage() {
  const router = useRouter();
  const handleBackToListClick = () => {
    router.push(`/support-cases`);
  };

  return (
    <div className="container-fluid">
      <PageMeta title="support cases" />
      <div className="row">
        <div className="col-lg-3"></div>
        <div className="col-lg-6">
          <div className="d-flex justify-content-start mb-2">
            <button
              type="button"
              className="btn btn-primary"
              onClick={() => handleBackToListClick()}
            >
              Back to List
            </button>
          </div>
          <SupportCaseOpenBox/>
        </div>
        <div className="col-lg-3"></div>
      </div>
      <style jsx>{``}</style>
    </div>
  );
}

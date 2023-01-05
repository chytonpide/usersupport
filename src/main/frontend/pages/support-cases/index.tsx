import PageMeta from "../../features/common/components/PageMeta";
import React, { useEffect, useState } from "react";
import { getSupportCases } from "../../features/supportcase/api/supportCaseApi";
import { format } from "../../utils/TimeUtils";
import { Pagination } from "../../features/common/components/Pagination";
import { useRouter } from "next/router";
import { Role } from "../../features/authentication/data/Role";
import Loading from "../../features/common/components/Loading";
import * as E from "fp-ts/Either";
import {useAuthTenant} from "../../features/authentication/hooks/useAuthTenant";
import {useAuthUser} from "../../features/authentication/hooks/useAuthUser";
import {SupportCases} from "../../features/supportcase/data/SupportCases";
import {StatusBadge} from "../../features/supportcase/components/StatusBadge";

export default function ListPage() {
  const tenant = useAuthTenant();
  const user = useAuthUser();
  const router = useRouter();
  const handleClick = (id: string) => {
    router.push(`/support-cases/${id}`);
  };

  const limit: number = 10;
  const [supportCases, setSupportCases] = useState<SupportCases>();
  const [page, setPage] = useState(1);
  const [offset, setOffset] = useState(0)
  const [errorMessage, setErrorMessage] = useState("");
  const handlePageIndexClick = (pageIndex: number) => {
    setOffset((pageIndex - 1) * limit)
    setPage(pageIndex);
  };

  const handleAddSupportCaseClick = () => {
    router.push(`/support-cases/open`);
  };

  useEffect(() => {
    const fetchData = async () => {
      if (!tenant || !user) {
        return;
      }
      const eitherData = await getSupportCases(tenant.id, offset, limit);
      if (E.isRight(eitherData)) {
        setSupportCases(eitherData.right);
      } else {
        setErrorMessage(eitherData.left);
      }
    };
    fetchData();
  }, [user, tenant, offset]);

  return (
    <>
      {!tenant || !user ? (
        <Loading />
      ) : (
        <>
          <div className="container-fluid">
            <PageMeta title="support cases" />
            {errorMessage && <div className="text-warning text-center">{errorMessage}</div>}
            {!supportCases && !errorMessage && <h4>Loading...</h4>}
            {supportCases && (
              <>
                {user.role === Role.CUSTOMER && (
                  <div className="d-flex justify-content-end mb-4">
                    <button
                      type="button"
                      className="btn btn-primary"
                      onClick={() => handleAddSupportCaseClick()}
                    >
                      Open Support Case
                    </button>
                  </div>
                )}

                <table className="table table-dark table-striped text-center">
                  <thead>
                    <tr>
                      <th className="col-6">Subject</th>
                      <th className="col-3">Status</th>
                      <th className="col-3">Opened At</th>
                    </tr>
                  </thead>
                  <tbody>
                    {supportCases &&
                      supportCases.items.map((supportCasesItem) => (
                        <tr
                          key={supportCasesItem.id}
                          onClick={() => handleClick(supportCasesItem.id)}
                          className="cursor-pointer"
                        >
                          <th>{supportCasesItem.subject}</th>
                          <td>
                            <StatusBadge status={supportCasesItem.status}/>

                          </td>
                          <td className="text-right">
                            {format(supportCasesItem.openedAt)}
                          </td>
                        </tr>
                      ))}
                  </tbody>
                </table>
                {supportCases && (
                  <div className="d-flex justify-content-center">
                    <Pagination
                      limit={supportCases.limit}
                      total={supportCases.total}
                      selectedPageIndex={page}
                      onPageIndexClick={handlePageIndexClick}
                    />
                  </div>
                )}
              </>
            )}
          </div>
        </>
      )}
    </>
  );
}

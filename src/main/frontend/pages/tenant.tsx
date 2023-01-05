import React, { useState } from "react";
import { useRouter } from "next/router";
import PageMeta from "../features/common/components/PageMeta";
import RoundedBlock from "../features/common/components/RoundedBlock";
import RoundedBlockHeader from "../features/common/components/RoundedBlockHeader";
import { v4 as uuidv4 } from "uuid";
import { signUp } from "../features/authentication/auth";
import { Role } from "../features/authentication/data/Role";
import {postDemoProvisioning} from "../features/authentication/api/provisionApi";

export const getStaticProps = async () => ({
  props: {
    authRequired: false,
  },
});

export default function Tenant() {
  const router = useRouter();
  const [tenantName, setTenantName] = useState("");
  const [loading, setLoading] = useState(false);
  const handleTenantNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTenantName(e.currentTarget.value);
  };

  const handleNextClick = () => {
    const tenantId = "ea1ba68c-51cb-44ec-b94e-1d4d9f063b92"
    //const tenantId = uuidv4();

    const tenant = {
      id: tenantId,
      name: tenantName,
    };

    const users = [
      {
        role: Role.CUSTOMER,
        name: "John",
        id: uuidv4(),
      },
      {
        role: Role.CUSTOMER,
        name: "Sofia",
        id: uuidv4(),
      },
      {
        role: Role.CUSTOMER,
        name: "Mark",
        id: uuidv4(),
      },
      {
        role: Role.SUPPORTER,
        name: "Steve",
        id: uuidv4(),
      },
      {
        role: Role.SUPPORTER,
        name: "Grace",
        id: uuidv4(),
      },
    ];

    const reqPayload = {
      tenant: tenant,
      users: users
    }
    /*
    postDemoProvisioning({
      tenant: tenant,
      users: users
    })
     */
    signUp(tenant, users);
    router.push("/users");
  };

  return (
    <div className="wrapper">
      <PageMeta title="provisioning" />
      <RoundedBlock>
        <PageMeta title="provisioning" />
        <RoundedBlockHeader>
          <div className="p-3">
            <h3>Demo Tenant</h3>
          </div>
        </RoundedBlockHeader>
        <div className="p-3">
          <div>
            <label htmlFor="tenantNameInput" className="form-label fs-5">
              tenant name
            </label>
            <input
              type="text"
              id="tenantNameInput"
              className={
                loading
                  ? "form-control form-control-lg disabled-input"
                  : "form-control form-control-lg"
              }
              value={tenantName}
              onChange={(e) => handleTenantNameChange(e)}
            />
          </div>
          <div className="d-flex justify-content-end mt-3">
            <button
              disabled={loading}
              type="button"
              className="btn btn-lg btn-primary"
              onClick={handleNextClick}
            >
              Next
            </button>
          </div>
        </div>
      </RoundedBlock>

      <style jsx>{`
        .wrapper {
          margin-top: calc(15vh);
        }
      `}</style>
    </div>
  );
}

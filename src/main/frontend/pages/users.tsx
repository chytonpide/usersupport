import PageMeta from "../features/common/components/PageMeta";
import RoundedBlock from "../features/common/components/RoundedBlock";
import RoundedBlockHeader from "../features/common/components/RoundedBlockHeader";
import React, {useEffect, useState} from "react";
import { useRouter } from "next/router";
import {getUsers, signInUser, signOutTenant} from "../features/authentication/auth";
import {Role} from "../features/authentication/data/Role";
import {useUsers} from "../features/authentication/hooks/useUsers";
import {User} from "../features/authentication/data/User";



export default function Users() {
  const [loading, setLoading] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState("");
  const router = useRouter();
  const users = useUsers();

  const handleSignOutTenantClick = () => {
    signOutTenant();
    router.push("/tenant");
  };

  const handleUserChange = (userId: string) => {
    setSelectedUserId(userId);
  };

  let selectedUser: User | null = null;

  if (users) {
    for (let user of users) {
      if (user.id === selectedUserId) selectedUser = user;
    }
  }

  let signInButtonLabel = "Sign in";

  if (selectedUser) {
    signInButtonLabel =
      "Sign in as " + selectedUser.role + " " + selectedUser.name;
  }

  const handleSignInClick = () => {
    if (selectedUser) {
      signInUser(selectedUser);
      router.push("/support-cases");
    }
  };

  if (!users) {
    return <>loading..</>;
  }

  return (
    <div className="wrapper">
      <PageMeta title="users" />
      <RoundedBlock>
        <PageMeta title="provisioning" />
        <RoundedBlockHeader>
          <div className="d-flex justify-content-between">
            <div className="p-3">
              <h3>Demo Users</h3>
            </div>
            <div className="d-flex align-items-center me-3">
              <button
                className="btn btn-warning"
                onClick={handleSignOutTenantClick}
              >
                sign out tenant
              </button>
            </div>
          </div>
        </RoundedBlockHeader>
        <div className="p-3">
          <div>
            <h5>Please select a role player</h5>
          </div>
          <div>
            <ul className="list-group">
              {users.map((user) => {
                return (
                  <li
                    key={user.id}
                    id={user.id}
                    className={
                      selectedUserId === user.id
                        ? "list-group-item active"
                        : "list-group-item"
                    }
                    onClick={() => {
                      handleUserChange(user.id);
                    }}
                  >
                    <span
                      className={
                        user.role === Role.SUPPORTER
                          ? "bg-warning text-dark badge fs-6 me-1"
                          : "bg-success badge fs-6 me-1"
                      }
                    >
                      {user.role}
                    </span>
                    {user.name}
                  </li>
                );
              })}
            </ul>
          </div>

          <div className="d-flex justify-content-end mt-3">
            <button
              disabled={loading||!selectedUser}
              type="button"
              className="btn btn-lg btn-primary"
              onClick={handleSignInClick}
            >
              {signInButtonLabel}
            </button>
          </div>
        </div>
      </RoundedBlock>

      <style jsx>{`
        .wrapper {
          margin-top: calc(5vh);
        }
      `}</style>
    </div>
  );
}

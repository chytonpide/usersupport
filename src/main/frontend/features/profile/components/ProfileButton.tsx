import React, { useState } from "react";
import { useRouter } from "next/router";
import { signOutUser} from "../../authentication/auth";
import { Role } from "../../authentication/data/Role";
import {useAuthUser} from "../../authentication/hooks/useAuthUser";

type Props = {};

const ProfileButton: React.FC<Props> = (prop) => {
  const router = useRouter();
  const authUser = useAuthUser();
  const handleSignOutUserClick = () => {
    signOutUser()
    router.push("/users")
  }

  return (
    <>
      {authUser && (
        <div className="dropdown">
          <button
            type="button"
            className="btn btn-secondary "
            data-bs-toggle="dropdown"
            data-bs-display="static" aria-expanded="false"
          >
            <div>


            </div>
            <div className="name">{authUser.name}<i className="bi bi-caret-down-fill icon-sm ms-1"></i></div>
            <span
              className={
                authUser.role === Role.SUPPORTER
                  ? "bg-warning text-dark badge fs-smt me-1"
                  : "bg-success badge fs-smt me-1"
              }
            >
              {authUser.role}
            </span>
          </button>
          <ul className="dropdown-menu dropdown-menu-end">
            <li className="dropdown-item cursor-pointer" onClick={handleSignOutUserClick}>
              Sign out
            </li>
          </ul>
        </div>
      )}

      <style jsx>{`
        .icon-lg {
          font-size: 1.3rem;
        }

        .icon-sm {
          font-size: 1rem;
        }

        .fs-smt {
          font-size: 0.8rem;
        }

        .name {
          font-size: 1rem;
          text-align: center;
        }
      `}</style>
    </>
  );
};
export default ProfileButton;

import RoundedBlock from "../../common/components/RoundedBlock";
import React, { useState } from "react";
import RoundedBlockHeader from "../../common/components/RoundedBlockHeader";
import { Role } from "../../authentication/data/Role";
import EventList from "./EventList";
import {
  getSupportCaseAssigned,
  getSupportCaseClosed,
  patchSupportCaseAssignedToMe,
  postSupportCaseClosed,
} from "../api/supportCaseApi";
import * as E from "fp-ts/Either";
import { sleep } from "../../../utils/SleepUtils";
import YesOrNoDialog from "../../common/components/YesOrNoDialog";
import { Tenant } from "../../authentication/data/Tenant";
import { User } from "../../authentication/data/User";
import { SupportCase } from "../data/SupportCase";
import { SupportCaseStatus } from "../data/SupportCaseStatus";
import {StatusBadge} from "./StatusBadge";

type Props = {
  tenant: Tenant;
  user: User;
  supportCase: SupportCase;
  onEditClick: () => void;
  onSupportCaseModified: (editedSupportCase: SupportCase) => void;
};

const SupportCaseBoxDefaultMode: React.FC<Props> = ({
  tenant,
  user,
  supportCase,
  onEditClick,
  onSupportCaseModified,
}) => {
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [openYesOrNoDialog, setOpenYesOrNoDialog] = useState(false);

  const editAccess = supportCase.customer.id == user.id;
  const closeAccess =
    supportCase.customer.id == user.id || supportCase.supporter?.id == user.id;

  const handleCloseClick = () => {
    setOpenYesOrNoDialog(true);
  };

  const handleCloseYesClick = async () => {
    setLoading(true);
    setErrorMessage("");
    const result = await postSupportCaseClosed(tenant.id, supportCase.id, user);

    if (E.isRight(result)) {
      const eitherSupportCase = await getSupportCaseClosed(
        tenant.id,
        supportCase.id
      );
      if (E.isRight(eitherSupportCase)) {
        setOpenYesOrNoDialog(false);
        onSupportCaseModified(eitherSupportCase.right);
        setLoading(false);
      } else {
        setOpenYesOrNoDialog(false);
        setErrorMessage(
          "Succeeded to close but fail to load the support-case."
        );
        setLoading(false);
      }
    } else {
      setOpenYesOrNoDialog(false);
      setErrorMessage(result.left);
      setLoading(false);
    }
  };

  const handleCloseNoClick = () => {
    setOpenYesOrNoDialog(false);
  };

  const handleAssignToMeClick = async () => {
    setErrorMessage("");
    setLoading(true);
    await sleep(5000);
    const result = await patchSupportCaseAssignedToMe(
      tenant.id,
      supportCase.id,
      user
    );

    if (E.isRight(result)) {
      const eitherSupportCase = await getSupportCaseAssigned(
        tenant.id,
        supportCase.id
      );
      if (E.isRight(eitherSupportCase)) {
        setLoading(false);
        onSupportCaseModified(eitherSupportCase.right);
      } else {
        setErrorMessage(
          "Succeeded to assign but fail to load the support-case."
        );
        setLoading(false);
      }
    } else {
      setErrorMessage(result.left);
      setLoading(false);
    }
  };
  return (
    <>
      <RoundedBlock>
        <RoundedBlockHeader>
          <div className="d-flex justify-content-between p-2">
            <div className="d-flex flex-column">
              <div className="align-self-center">
                <label>Customer</label>
              </div>
              <div className="align-self-center fw-bold">
                {supportCase.customer.name}
              </div>
            </div>
            <div>
              <div className="d-flex justify-content-end">
                <EventList events={supportCase.events} />
                {supportCase.status != SupportCaseStatus.CLOSED && (
                  <>
                    {editAccess && (
                      <button
                        onClick={onEditClick}
                        type="button"
                        className="btn btn-primary me-2"
                      >
                        Edit
                      </button>
                    )}
                    {closeAccess && (
                      <button
                        onClick={() => handleCloseClick()}
                        type="button"
                        className="btn btn-secondary"
                      >
                        Close
                      </button>
                    )}
                    {user.role === Role.SUPPORTER &&
                      supportCase.supporter === undefined && (
                        <button
                          disabled={loading}
                          onClick={() => handleAssignToMeClick()}
                          type="button"
                          className="btn btn-primary me-2"
                        >
                          Assign to me{" "}
                          {loading && (
                            <div
                              className="spinner-border spinner-border-sm text-light"
                              role="status"
                            >
                              <span className="visually-hidden">
                                Loading...
                              </span>
                            </div>
                          )}
                        </button>
                      )}
                  </>
                )}
              </div>

              {errorMessage && (
                <div className="d-flex justify-content-end">
                  <span className="fs-7 text-warning p-2">{errorMessage}</span>
                </div>
              )}
            </div>
          </div>
        </RoundedBlockHeader>
        <div className="m-3">
          <div className="mb-3">
            <label>Status</label>
            <h5>
              <StatusBadge status={supportCase.status} />
            </h5>
          </div>
          {supportCase.supporter && (
            <div className="mb-3">
              <label>Assigned Supporter</label>
              <input
                type="text"
                className="form-control disabled-input"
                defaultValue={supportCase.supporter.name}
                disabled
                readOnly
              />
            </div>
          )}

          <div className="mb-3">
            <label>Subject</label>
            <input
              type="text"
              className="form-control disabled-input"
              defaultValue={supportCase.subject}
              disabled
              readOnly
            />
          </div>
          <div className="mb-3">
            <label>Category</label>

            <select
              className="form-select disabled-input"
              disabled
              defaultValue={supportCase.categoryId}
            >
              <option value="">Open this select menu</option>
              <option value="cat1">cat1</option>
              <option value="cat2">cat2</option>
              <option value="cat3">cat3</option>
            </select>
          </div>
          <div className="mb-3">
            <label>Description</label>
            <div className="form-control disabled-input">
              <pre>{supportCase.description}</pre>
            </div>
          </div>
          <div className="mb-3"></div>
        </div>
      </RoundedBlock>
      <YesOrNoDialog
        open={openYesOrNoDialog}
        onYesClick={handleCloseYesClick}
        onNoClick={handleCloseNoClick}
        messages={["Are you sure to close it?"]}
        loading={loading}
      />
    </>
  );
};
export default SupportCaseBoxDefaultMode;

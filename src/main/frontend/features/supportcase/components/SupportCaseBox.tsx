import React, { useState } from "react";
import SupportCaseBoxDefaultMode from "./SupportCaseBoxDefaultMode";
import SupportCaseBoxEditMode from "./SupportCaseBoxEditMode";
import {Tenant} from "../../authentication/data/Tenant";
import {User} from "../../authentication/data/User";
import {SupportCase} from "../data/SupportCase";


type Props = {
  tenant: Tenant;
  user: User;
  supportCase: SupportCase;
};

const SupportCaseBox: React.FC<Props> = (props) => {

  const [supportCase, setSupportCase] = useState(props.supportCase);
  const [editMode, setEditMode] = useState(false);

  const handleSupportCaseModified = (changedSupportCase: SupportCase) => {
    let newSupportCase = changedSupportCase;

    setSupportCase(newSupportCase);
    setEditMode(false);
  };



  return (
    <>
      {!editMode ? (
        <SupportCaseBoxDefaultMode
          tenant={props.tenant}
          user={props.user}
          supportCase={supportCase}
          onEditClick={() => {
            setEditMode(true);
          }}
          onSupportCaseModified={handleSupportCaseModified}
        />
      ) : (
        <SupportCaseBoxEditMode
          tenant={props.tenant}
          user={props.user}
          supportCase={supportCase}
          onCancelClick={() => {
            setEditMode(false);
          }}
          onSupportCaseModified={handleSupportCaseModified}
        />
      )}


    </>
  );
};
export default SupportCaseBox;

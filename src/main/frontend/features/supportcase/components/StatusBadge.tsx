import React, { useState } from "react";
import { SupportCaseStatus } from "../data/SupportCaseStatus";

type Props = {
  status: SupportCaseStatus;
};

export const StatusBadge: React.FC<Props> = ({status}) => {

  let bgClass = "";

  switch (status) {
    case SupportCaseStatus.OPENED :
      bgClass = "bg-light text-dark"
      break;
    case SupportCaseStatus.ASSIGNED :
      bgClass = "bg-warning  text-dark"
      break;
    case SupportCaseStatus.CLOSED :
      bgClass = "bg-success"
      break;
  }


  return <span className={`badge me-1 ${bgClass}`}>{status}</span>;
};



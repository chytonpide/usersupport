import { NETWORK_ERROR_MESSAGE } from "../../common/api/errorMessages";
import * as E from "fp-ts/Either";
import { userToken } from "../../authentication/auth";
import { sleep } from "../../../utils/SleepUtils";
import {SupportCase} from "../data/SupportCase";
import {SupportCases} from "../data/SupportCases";
import PostSupportCasePayload = app.data.request.PostSupportCasePayload;
import {User} from "../../authentication/data/User";
import PatchSupportCaseContentPayload = app.data.request.PatchSupportCaseContentPayload;

const API_ORIGIN = process.env.API_ORIGIN;
const MAX_RETRY_COUNT = 3;

export const getSupportCases = async (
  tenantId: string,
  offset: number,
  limit: number
) => {
  try {
    const url = `${API_ORIGIN}/tenants/${tenantId}/support-cases?offset=${offset}&limit=${limit}`;

    const response = await fetch(url);
    const payload = await response.json();
    const typedPayload = payload as SupportCases;

    return E.right(typedPayload);
  } catch (err) {
    return E.left(NETWORK_ERROR_MESSAGE);
  }
};

export const getSupportCase = async (
  tenantId: string,
  supportCaseId: string
) => {
  try {
    const url = `${API_ORIGIN}/tenants/${tenantId}/support-cases/${supportCaseId}`;

    const response = await fetch(url);
    const payload = await response.json();
    const typedPayload = payload as SupportCase;

    return E.right(typedPayload);
  } catch (err) {
    return E.left(NETWORK_ERROR_MESSAGE);
  }
};


export const getEditedSupportCaseAfterTime = async (
  tenantId: string,
  supportCaseId: string,
  time: number | undefined
) => {
  for (let retries = 0; retries < MAX_RETRY_COUNT ; retries++) {
    const eitherSupportCase = await getSupportCase(tenantId, supportCaseId);
    if (E.isRight(eitherSupportCase)) {
      if (eitherSupportCase.right.lastEditedAt != time) {
        return eitherSupportCase;
      }
    } else {
      return eitherSupportCase;
    }
    await sleep(1000);
  }
  return E.left("Failed to get the support case was edited after the time.");
};

export const getSupportCaseAssigned = async (
  tenantId: string,
  supportCaseId: string
) => {
  for (let retries = 0; retries < MAX_RETRY_COUNT ; retries++) {
    const eitherSupportCase = await getSupportCase(tenantId, supportCaseId);
    if (E.isRight(eitherSupportCase)) {
      console.log(eitherSupportCase.right);

      if (eitherSupportCase.right.assignedAt) {
        return eitherSupportCase;
      }
    } else {
      return eitherSupportCase;
    }
    await sleep(1000);
  }
  return E.left("Failed to get assigned support case.");
};


export const getSupportCaseClosed = async (
  tenantId: string,
  supportCaseId: string
) => {
  for (let retries = 0; retries < MAX_RETRY_COUNT ; retries++) {
    const eitherSupportCase = await getSupportCase(tenantId, supportCaseId);
    if (E.isRight(eitherSupportCase)) {
      console.log(eitherSupportCase.right);

      if (eitherSupportCase.right.closedAt) {
        return eitherSupportCase;
      }
    } else {
      return eitherSupportCase;
    }
    await sleep(1000);
  }
  return E.left("Failed to get closed support case.");
};

export const postSupportCase = async (
  tenantId: string,
  user: User,
  requestPayload: PostSupportCasePayload
) => {
  try {
    const url = `${API_ORIGIN}/tenants/${tenantId}/support-cases`;
    const response = await fetch(url, {
      method: "post",
      mode: "cors",
      headers: {
        Authorization: userToken(user),
        "Content-Type": "application/json;charset=utf-8",
      },
      body: JSON.stringify(requestPayload),
    });

    if (response.status == 201) {
      const location = response.headers.get("Location");
      if (location) {
        const supportCaseId = location.substring(location.lastIndexOf("/") + 1);
        return E.right(supportCaseId);
      } else {
        return E.left(NETWORK_ERROR_MESSAGE);
      }
    } else if (response.status == 400) {
      const errorPayload = await response.json();
      return E.left(errorPayload.messages);
    } else {
      return E.left(NETWORK_ERROR_MESSAGE);
    }
  } catch (err) {
    return E.left(NETWORK_ERROR_MESSAGE);
  }
};

export const postSupportCaseClosed = async (
  tenantId: string,
  supportCaseId: string,
  user: User
) => {
  try {
    const url = `${API_ORIGIN}/tenants/${tenantId}/support-cases/${supportCaseId}/closed`;
    const response = await fetch(url, {
      method: "post",
      mode: "cors",
      headers: {
        Authorization: userToken(user),
        "Content-Type": "application/json;charset=utf-8",
      },
    });

    if (response.status == 204) {
      return E.right("");
    } else if (response.status == 400) {
      const errorPayload = await response.json();
      return E.left(errorPayload.messages);
    } else {
      return E.left(NETWORK_ERROR_MESSAGE);
    }
  } catch (err) {
    return E.left(NETWORK_ERROR_MESSAGE);
  }
};

export const patchSupportCaseContent = async (
  tenantId: string,
  supportCaseId: string,
  user: User,
  requestPayload: PatchSupportCaseContentPayload
) => {
  try {
    const url = `${API_ORIGIN}/tenants/${tenantId}/support-cases/${supportCaseId}/content`;
    const response = await fetch(url, {
      method: "PATCH",
      mode: "cors",
      headers: {
        Authorization: userToken(user),
        "Content-Type": "application/json;charset=utf-8",
      },
      body: JSON.stringify(requestPayload),
    });

    if (response.status == 204) {
      return E.right("");
    } else if (response.status == 400) {
      const errorPayload = await response.json();
      return E.left(errorPayload.messages);
    } else {
      return E.left(NETWORK_ERROR_MESSAGE);
    }
  } catch (err) {
    return E.left(NETWORK_ERROR_MESSAGE);
  }
};

export const patchSupportCaseAssignedToMe = async (
  tenantId: string,
  supportCaseId: string,
  user: User
) => {
  try {
    const url = `${API_ORIGIN}/tenants/${tenantId}/support-cases/${supportCaseId}/assigned-to-me`;
    const response = await fetch(url, {
      method: "PATCH",
      mode: "cors",
      headers: {
        Authorization: userToken(user),
        "Content-Type": "application/json;charset=utf-8",
      },
    });

    if (response.status == 204) {
      return E.right("");
    } else if (response.status == 400) {
      const errorPayload = await response.json();
      return E.left(errorPayload.messages);
    } else {
      return E.left(NETWORK_ERROR_MESSAGE);
    }
  } catch (err) {
    return E.left(NETWORK_ERROR_MESSAGE);
  }
};

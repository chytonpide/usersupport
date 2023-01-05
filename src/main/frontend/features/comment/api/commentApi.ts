import * as E from "fp-ts/Either";
import {NETWORK_ERROR_MESSAGE} from "../../common/api/errorMessages";
import PostCommentPayload = app.data.request.PostCommentPayload;
import PatchCommentBodyPayload = app.data.request.PutCommentContentPayload;
import {userToken} from "../../authentication/auth";
import {User} from "../../authentication/data/User";
import {Comment} from "../data/Comment";
const API_ORIGIN = process.env.API_ORIGIN;

export const getComment = async (
  tenantId: string,
  commentId: string
) => {
  try {
    const url = `${API_ORIGIN}/tenants/${tenantId}/comments/${commentId}`;
    const response = await fetch(url);
    const payload = await response.json();
    const typedPayload = payload as Comment;
    return E.right(typedPayload);
  } catch (err) {
    return E.left(NETWORK_ERROR_MESSAGE);
  }
};

export const postComment = async (
  tenantId: string,
  user: User,
  requestPayload: PostCommentPayload
) => {
  try {
    const url = `${API_ORIGIN}/tenants/${tenantId}/comments`;
    const response = await fetch(url, {
      method: "post",
      mode: "cors",
      headers: {
        "Content-Type": "application/json;charset=utf-8",
        "Authorization": userToken(user),
      },
      body: JSON.stringify(requestPayload),
    });

    if (response.status == 201) {
      const location = response.headers.get("Location")
      if(location) {
        const commentId = location.substring(location.lastIndexOf("/")+1)
        return E.right(commentId);
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
}

export const patchCommentBody = async (tenantId: string, commentId: string, user: User, requestPayload: PatchCommentBodyPayload) => {
  try {

    const url = `${API_ORIGIN}/tenants/${tenantId}/comments/${commentId}/body`
    console.log(url)
    const response = await fetch(url, {
      method: "PATCH",
      mode: "cors",
      headers: {
        "Content-Type": "application/json;charset=utf-8",
        "Authorization": userToken(user),
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
}

export const deleteComment = async (tenantId: string, commentId: string, user: User) => {
  try {

    const url = `${API_ORIGIN}/tenants/${tenantId}/comments/${commentId}`
    console.log(url)
    console.log(userToken(user))

    const response = await fetch(url, {
      method: "DELETE",
      mode: "cors",
      headers: {
        "Content-Type": "application/json;charset=utf-8",
        "Authorization": userToken(user),
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
}


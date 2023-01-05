import PostDemoProvisioningPayload = app.data.request.PostDemoProvisioningPayload;

export const postDemoProvisioning = async (
  requestPayload: PostDemoProvisioningPayload
) => {
  const API_ORIGIN = process.env.API_ORIGIN;
  const url =
    API_ORIGIN +
    "/demo-provisioning";

  const req = {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestPayload),
  }


  const response = await fetch(url, req);
  return response;
};

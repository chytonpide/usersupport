const delay = 1000

export const errorResponse = () => {
  return new Promise<Response>((resolver) => {
    setTimeout(() => {
      const headers = {
        status: 400, //created
        statusText: "Bad Request",
      };
      const payload = {
        message: "bad request.",
      };
      resolver(new Response(JSON.stringify(payload), headers));
    }, delay);
  });
};


export const okResponse = () => {
  return new Promise<Response>((resolver) => {
    setTimeout(() => {
      const headers = {
        status: 200,
        statusText: "OK",
      };
      resolver(new Response(JSON.stringify(""), headers));
    }, delay);
  });
};


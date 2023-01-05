import { PATH_TO_LAYOUT_NAME } from "../../configs/pathToLayoutName";

export const pathToLayoutName = (aPath: string) => {
  let path = aPath;

  const wildCardPaths = Object.keys(PATH_TO_LAYOUT_NAME).filter((path) => {
    return path.endsWith("*");
  });

  wildCardPaths.forEach((wildCardPath) => {
    if (path.startsWith(wildCardPath.slice(0,-1))) {
      path = wildCardPath;
    }
  });

  const typedKey = path as keyof typeof PATH_TO_LAYOUT_NAME;
  const result = PATH_TO_LAYOUT_NAME[typedKey];

  return result;
};

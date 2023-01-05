import {useEffect, useState} from "react";
import {getUsers} from "../auth";
import {User} from "../data/User";

export const useUsers = () => {
  const [users, setUsers] = useState<User[]|null>(null);

  useEffect(() => {
    setUsers(getUsers());
  },[JSON.stringify(users)])

  return users
};

import {Tenant} from "../../authentication/data/Tenant";
import {Commenter} from "./Commenter";

export interface Comment {
  id: string;
  tenant: Tenant;
  commenter: Commenter;
  body: string;
  commentedAt: number;
  lastEditedAt: number;
}

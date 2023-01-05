import { Customer } from "../../common/data/Customer";
import {SupportCaseStatus} from "./SupportCaseStatus";

export interface SupportCaseListItem {
  id: string;
  customer: Customer;
  categoryId: string;
  subject: string;
  status: SupportCaseStatus;
  openedAt: number;
}

import {Customer} from "../../common/data/Customer";
import {Supporter} from "../../common/data/Supporter";
import {CommentListItem} from "./CommentListItem";
import {Event} from "./Event"
import {SupportCaseStatus} from "./SupportCaseStatus";

export interface SupportCase {
    id: string;
    customer: Customer;
    categoryId: string;
    subject: string;
    description: string;
    status: SupportCaseStatus;
    supporter: Supporter;
    comments: CommentListItem[];
    events: Event[];
    openedAt: number;
    assignedAt: number;
    closedAt: number;
    lastEditedAt: number;
  }


import SupportCaseListItem = app.data.SupportCaseListItem;

export interface SupportCases {
  items: SupportCaseListItem[];
  offset: number;
  limit: number;
  total: number;
}
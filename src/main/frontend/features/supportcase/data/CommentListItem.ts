import Commenter = app.data.Commenter;

export interface CommentListItem {
  id: string;
  commenter: Commenter;
  body: string;
  commentedAt: number;
  lastEditedAt: number;
}

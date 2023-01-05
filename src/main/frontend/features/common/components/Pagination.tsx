const DISPLAYED_PAGE_INDEX_LIMIT = 5;

const getDisplayedPageIndices = (
  selectedPageIndex: number,
  totalPageNum: number
) => {
  let displayedStartIndex: number;
  const quotient = (selectedPageIndex / DISPLAYED_PAGE_INDEX_LIMIT) >> 0;
  const remainder = selectedPageIndex % DISPLAYED_PAGE_INDEX_LIMIT;

  if (remainder == 0) {
    displayedStartIndex = (quotient - 1) * DISPLAYED_PAGE_INDEX_LIMIT + 1;
  } else {
    displayedStartIndex = quotient * DISPLAYED_PAGE_INDEX_LIMIT + 1;
  }

  let displayedPageIndexLength = DISPLAYED_PAGE_INDEX_LIMIT;
  if (displayedStartIndex + DISPLAYED_PAGE_INDEX_LIMIT > totalPageNum)
    displayedPageIndexLength =
      totalPageNum - displayedStartIndex + 1;

  const results = Array<number>(displayedPageIndexLength)
    .fill(0)
    .map((_, i) => displayedStartIndex + i);
  return results;
};

interface Props {
  limit: number;
  total: number;
  selectedPageIndex: number;
  onPageIndexClick: (pageIndex: number) => void;
}

export const Pagination: React.FC<Props> = ({
  limit,
  total,
  selectedPageIndex,
  onPageIndexClick,
}) => {

  if (selectedPageIndex < 1) onPageIndexClick(1);

  const totalPageNum = Math.ceil(total / limit);

  if (selectedPageIndex > totalPageNum) onPageIndexClick(totalPageNum);

  const displayedPageIndices = getDisplayedPageIndices(
    selectedPageIndex,
    totalPageNum
  );

  return (
    <nav>
      <ul className="pagination">
        <li className="page-item">
          <span
            className="page-link cursor-pointer"
            onClick={() => onPageIndexClick(selectedPageIndex - 1)}
          >
            &laquo;
          </span>
        </li>

        {displayedPageIndices.map((pageIndex) => {
          return (
            <li
              className={
                (selectedPageIndex === pageIndex ? "active " : "") +
                "page-item cursor-pointer"
              }
              key={pageIndex}
              onClick={() => onPageIndexClick(pageIndex)}
            >
              <span className="page-link">{pageIndex}</span>
            </li>
          );
        })}

        <li className="page-item">
          <span
            className="page-link cursor-pointer"
            onClick={() => onPageIndexClick(selectedPageIndex + 1)}
          >
            &raquo;
          </span>
        </li>
      </ul>
      <style jsx>{`
        .pagination > .page-item > .page-link {
          border-color: #373b3e;
          background-color: #2c3034;
          color: #eeeeee;
        }

        .pagination > .page-item.active > .page-link {
          color: #ffffff;
          background-color: #212529;
        }
      `}</style>
    </nav>
  );
};

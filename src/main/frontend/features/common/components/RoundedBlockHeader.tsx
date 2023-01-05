import React from "react";

type Props = {
  children: JSX.Element;
};

const RoundedBlockHeader: React.FC<Props> = ({ children }) => {
  return (
    <div className="rounded-block-header">
      {children}

      <style jsx>{`
        .rounded-block-header {
          border-bottom: solid #a8a8a8;
        }
      `}</style>
    </div>
  );
};
export default RoundedBlockHeader;

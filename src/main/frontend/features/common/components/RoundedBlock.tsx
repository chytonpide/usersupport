import React from "react";

type Props = {
  children: React.ReactNode;
};

const RoundedBlock: React.FC<Props> = ({ children }) => {
  return (
    <div className="rounded-block">
      {children}

      <style jsx>{`
        .rounded-block {
          border-radius: 0.375rem;
          border: solid #a8a8a8;
        }

        .rounded-block > .header {
          border-bottom: solid #a8a8a8;
        }

        label {
          font-size: 0.9rem !important;
          color: #cccccc;
        }

        .disabled-input {
          color: #eeeeee;
          background: #37373d;
          border-color: #37373d;
        }
      `}</style>
    </div>
  );
};
export default RoundedBlock;

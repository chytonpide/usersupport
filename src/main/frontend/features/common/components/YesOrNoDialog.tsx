import Modal from "react-modal";
import React from "react";

const customStyles = {
  content: {
    top: "40%",
    left: "50%",
    right: "auto",
    bottom: "auto",
    marginRight: "-50%",
    transform: "translate(-50%, -50%)",
    padding: 0,
    border: 0,
    backgroundColor: "transparent",
    borderRadius: 0,
  },

  overlay: {
    background: "rgba(0, 0, 0, 0.7)",
  },
};

type Props = {
  onYesClick: () => void;
  onNoClick: () => void;
  messages: string[];
  loading: boolean;
  open: boolean;
};

const YesOrNoDialog: React.FC<Props> = ({
  onYesClick,
  onNoClick,
  messages,
  loading,
  open,
}) => {
  return (
    <>
      <Modal isOpen={open} style={customStyles}>
        <div className="modal-block">
          <div className="text-light">
            <div className="ms-3 me-3 mt-1 mb-3">
              {messages.map((message, index) => {
                return <div key={index}>{message}</div>;
              })}
            </div>
            <div className="d-flex">
              <button
                disabled={loading}
                type="button"
                className="btn btn-secondary me-2 flex-fill"
                onClick={() => {
                  onNoClick();
                }}
              >
                No
              </button>

              <button
                disabled={loading}
                type="button"
                className="btn btn-primary flex-fill"
                onClick={() => {
                  onYesClick();
                }}
              >
                Yes
                {loading && (
                  <div
                    className="spinner-border spinner-border-sm text-light ms-1"
                    role="status"
                  ><span className="visually-hidden">Loading...</span>
                  </div>
                )}
              </button>
            </div>
          </div>
        </div>
      </Modal>
      <style jsx>{`
        .modal-block {
          border-radius: 0.375rem;
          border: solid #a8a8a8;
          background: var(--bs-body-color);
          padding: 1rem;
        }
      `}</style>
    </>
  );
};

export default YesOrNoDialog;

const Loading: React.FC = () => (
  <div className="d-flex justify-content-center">
    <div className="d-flex flex-column ">
      <div className="spinner-border m-auto" role="status">
        <span className="visually-hidden">Loading...</span>
      </div>
      <div className="mt-3">Loading...</div>
    </div>
  </div>
);
export default Loading;

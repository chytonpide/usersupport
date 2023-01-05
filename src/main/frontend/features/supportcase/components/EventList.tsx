import React, { useState } from "react";
import { format } from "../../../utils/TimeUtils";
import { Event } from "../data/Event"

type Props = {
  events: Event[];
};

const EventList: React.FC<Props> = (props) => {

  return (
    <div className="dropdown d-flex">
      <button
        type="button"
        className="btn btn-secondary me-2"
        data-bs-toggle="dropdown"
        aria-expanded="true"
      >
        History
      </button>
      <ul className="dropdown-menu dropdown-menu-end dropdown-menu-dark">
        <div className="mt-1 ms-2 me-2 mb-2">
        {props.events.map((event, i,array) => (
          <div key={i} className={array.length !== i + 1 ? "mb-3" : ""} >
            <div className="fs-8">{format(event.occurredAt)}</div>
            <div>{event.description}</div>
          </div>
        ))}
        </div>
      </ul>
    </div>
  );
};
export default EventList;

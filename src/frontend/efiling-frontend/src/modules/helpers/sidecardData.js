/* eslint-disable react/jsx-one-expression-per-line */
import React from "react";
import { MdInfoOutline, MdPerson, MdTimer } from "react-icons/md";
import { getJWTData } from "./authentication-helper/authenticationHelper";

const aboutCso = () => {
  return {
    heading: "About E-File Submission",
    content: [
      <p key="aboutCso">
        E-File submission is a service to help you securely and electronically
        file documents with the Government of British Columbia Court Services
        Online (CSO).&nbsp;
        <a
          href={`${sessionStorage.getItem("csoBaseUrl")}/about/index.do`}
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn more about CSO
        </a>
        .
      </p>,
    ],
    type: "bluegrey",
    isWide: true,
    icon: <MdInfoOutline className="side-card-icon" />,
  };
};

const csoAccountDetails = () => {
  let username = getJWTData().preferred_username;
  username = username.substring(0, username.indexOf("@"));

  return {
    heading: "Your CSO Account",
    content: [
      <p key="csoAccountDetails">
        CSO account <strong>{sessionStorage.getItem("csoAccountId")}</strong> is
        linked to your Basic BCeID account&nbsp;
        <strong>{username}</strong>
        &nbsp;and will be used to file documents.&nbsp;
        <a
          href={`${sessionStorage.getItem(
            "csoBaseUrl"
          )}/accounts/editProfile.do`}
          target="_blank"
          rel="noopener noreferrer"
        >
          View your CSO account details
        </a>
        .
      </p>,
    ],
    type: "bluegrey",
    isWide: true,
    icon: <MdPerson className="side-card-icon" />,
  };
};

const rushSubmission = (onClick) => {
  return {
    heading: "Rush/Urgent Submission",
    content: [
      <p key="rushSubmission">
        If you wish to request that this package be submitted on an urgent
        (rush) basis, you must provide a reason for your request to be
        considered.&nbsp;
        <span
          onKeyDown={onClick}
          role="button"
          tabIndex={0}
          className="file-href"
          onClick={onClick}
        >
          Request rush submission
        </span>
        .
      </p>,
    ],
    type: "bluegrey",
    isWide: true,
    icon: <MdTimer className="side-card-icon" />,
  };
};

const amendments = () => {
  return {
    heading: "Amendments",
    content: [
      <p key="amendments">
        Please indicate document uploads that are amendments - changes or
        alterations to existing, previously submitted documents.
      </p>,
    ],
    type: "bluegrey",
    isWide: true,
    icon: <MdInfoOutline className="side-card-icon" />,
  };
};

const supremeCourtScheduling = () => {
  return {
    heading: "Supreme Court Scheduling",
    content: [
      <p key="scs">
        Please indicate if your document upload has been approved for Supreme
        Court Scheduling...
      </p>,
    ],
    type: "bluegrey",
    isWide: true,
    icon: <MdInfoOutline className="side-card-icon" />,
  };
};

export function getSidecardData(onClick) {
  const aboutCsoCard = aboutCso();
  const csoAccountDetailsCard = csoAccountDetails();
  const rushSubmissionCard = rushSubmission(onClick);
  const amendmentsCard = amendments();
  const supremeCourtSchedulingCard = supremeCourtScheduling();

  return {
    aboutCso: aboutCsoCard,
    csoAccountDetails: csoAccountDetailsCard,
    rushSubmission: rushSubmissionCard,
    amendments: amendmentsCard,
    supremeCourtScheduling: supremeCourtSchedulingCard,
  };
}

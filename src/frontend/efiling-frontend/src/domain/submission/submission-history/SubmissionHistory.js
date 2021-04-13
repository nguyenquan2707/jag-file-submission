import React, { useState, useEffect } from "react";
import { Sidecard, Button } from "shared-components";
import { useLocation } from "react-router-dom";
import queryString from "query-string";
import { getSidecardData } from "../../../modules/helpers/sidecardData";
import SubmissionList from "./SubmissionList";
import { getSubmissionHistory } from "./SubmissionHistoryService";
import "./SubmissionHistory.scss";
import { Toast } from "../../../components/toast/Toast";

export default function SubmissionHistory() {
  const [show, setShow] = useState(false)
  const [submissions, setSubmissions] = useState([]);
  const [visibleSubmissions, setVisibleSubmissions] = useState([]);
  const [search, setSearch] = useState("");
  const aboutCsoSidecard = getSidecardData().aboutCso;
  const csoAccountDetailsSidecard = getSidecardData().csoAccountDetails;

  const location = useLocation();
  const queryParams = queryString.parse(location.search);
  const { applicationCode } = queryParams;

  useEffect(() => {
    getSubmissionHistory(applicationCode)
      .then((response) => {
        setSubmissions(response.data);
        setVisibleSubmissions(response.data);
      })
      .catch(() => setShow(true));
  }, [applicationCode]);

  function handlePackageSearch() {
    if (search !== "") {
      setVisibleSubmissions(
        submissions.filter((submission) =>
          String(submission.packageNumber).startsWith(String(search))
        )
      );
    } else {
      setVisibleSubmissions(submissions);
    }
  }

  const searchPackagesElement = (
    <>
      <h3>Search Packages</h3>
      <br />
      <span>Package Number</span>
      <br />
      <input
        data-testid="package-search"
        type="number"
        name="search"
        className="package-search"
        onChange={(e) => setSearch(e.target.value)}
      />
      <Button
        data-testid="btn-package-search"
        label="Search"
        styling="bcgov-normal-blue btn search-btn"
        onClick={handlePackageSearch}
      />
    </>
  );

  const submittedPackagesElement = (
    <>
      <div className="submission-list-block">
        <h3>Submitted Packages</h3>
        <div className="submission-list-container">
          <SubmissionList submissions={visibleSubmissions} />
        </div>
      </div>
    </>
  );

  return (
    <div className="ct-submission-history page">
      <div className="content col-md-8">
        <h1>Submission History</h1>
        <br />
        {searchPackagesElement}
        {show && (
          <Toast content="Something went wrong while trying to retrieve your submissions." setShow={setShow} />
        )}
        {submittedPackagesElement}
      </div>

      <div className="sidecard">
        <Sidecard sideCard={csoAccountDetailsSidecard} />
        <Sidecard sideCard={aboutCsoSidecard} />
      </div>
    </div>
  );
}

/* eslint-disable no-var */
/* eslint-disable  */
/* eslint-disable vars-on-top */
import React, { useState } from "react";
import { getTestData } from "../../../modules/test-data/confirmationPopupTestData";
import { getCourtData } from "../../../modules/test-data/courtTestData";
import { getDocumentsData } from "../../../modules/test-data/documentTestData";

import Upload from "./Upload";

export default {
  title: "Upload",
  component: Upload,
};

const confirmationPopup = getTestData();
const courtData = getCourtData();
const submissionId = "abc123";
const files = getDocumentsData();
const upload = {
  confirmationPopup,
  submissionId,
  courtData,
  files,
};

export var Default = function () {
  // eslint-disable-next-line no-unused-vars
  const [showUpload, setShowUpload] = useState(true);
  // eslint-disable-next-line no-unused-vars
  const [refreshFiles, setRefreshFiles] = useState(false);
  const uploadWithStates = { ...upload, setShowUpload, setRefreshFiles };
  return <Upload upload={uploadWithStates} />;
};

export var Mobile = function () {
  // eslint-disable-next-line no-unused-vars
  const [showUpload, setShowUpload] = useState(true);
  // eslint-disable-next-line no-unused-vars
  const [refreshFiles, setRefreshFiles] = useState(false);
  const uploadWithStates = { ...upload, setShowUpload, setRefreshFiles };
  return <Upload upload={uploadWithStates} />;
};

Mobile.parameters = {
  viewport: {
    defaultViewport: "mobile2",
  },
};

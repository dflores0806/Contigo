from pydantic import BaseModel, ValidationError
from typing import Optional
from flask.wrappers import Response as FlaskResponse

from flask_openapi3 import Info, Tag, FileStorage
from flask_openapi3 import OpenAPI

from flask import flash, send_from_directory
import os
from werkzeug.utils import secure_filename
import pathlib
import time

# Consts (paths and extensions)
FOLDER_USERS_CSV = "data/users_csv/"
FOLDER_USERS_CKPT = "data/users_ckpt/"
FOLDER_MODEL = "data/base_model/"
FOLDER_AGG_MODEL = "data/aggregated_ckpt/"
ALLOWED_EXTENSIONS = {"csv", "ckpt"}


# OpenApi validation model
class ValidationErrorModel(BaseModel):
    code: str
    message: str


def validation_error_callback(e: ValidationError) -> FlaskResponse:
    validation_error_object = ValidationErrorModel(code="400", message=e.json())
    response = validation_error_object.json()
    response.headers["Content-Type"] = "application/json"
    response.status_code = getattr(app, "validation_error_status", 422)
    return response


# Main app
info = Info(title="Contigo API", version="1.0.0", url_prefix='myapp')
app = OpenAPI(
    info=info,
    import_name='myapp',
    validation_error_status=400,
    validation_error_model=ValidationErrorModel,
    validation_error_callback=validation_error_callback,
)

# OpenApi tags
data_tag = Tag(
    name="data",
    description="Get and post users files on the servers. These files correspond to the ckpt model and csv raw data about their profile. With the ckpt files, the global model will be aggregated to be later downloaded.",
)
model_tag = Tag(name="model", description="Get the global model.")


# Endpoints models
class GetDataQuery(BaseModel):
    file: Optional[str] = None


class UploadFileForm(BaseModel):
    file: FileStorage
    user: str


# Retrieve a specific file or show the form
@app.get("/data", tags=[data_tag])
def get_data(query: GetDataQuery):
    """Get file (csv or ckpt)
    to get a specific file from the server test
    """
    # Retrieve files
    filename = query.file

    # Check if file is requested
    if filename != None:
        # Check if file exists
        ext = os.path.splitext(filename)[1]
        if ext == ".csv":
            fileExists = os.path.isfile(FOLDER_USERS_CSV + filename)
            if fileExists:
                return send_from_directory(FOLDER_USERS_CSV, filename), 201
            else:
                return "Error. File does not exist", 400
        elif ext == ".ckpt":
            fileExists = os.path.isfile(FOLDER_USERS_CKPT + filename)
            if fileExists:
                return send_from_directory(FOLDER_USERS_CKPT, filename), 201
            else:
                return "Error. File does not exist", 400
        else:
            return "Error. File does not allowed (csv or ckpt)", 400

    else:
        return (
            """
        <!doctype html>
        <title>Upload model</title>
        <h1>Upload new File</h1>
        <form method=post enctype=multipart/form-data>
        <input type=file name=file>
        <input type=text name=user value=User>
        <input type=submit value=Upload>
        </form>
        """,
            201,
        )


# Set a file on the server
@app.post("/data", tags=[data_tag])
def post_data(form: UploadFileForm):
    """Post file (csv or ckpt)
    to post a specific file on the server
    """
    file = form.file
    user = form.user
    # Send file
    if file == None:
        flash("No file part")
        return "File did not attached", 400
    timestr = time.strftime("%Y%m%d-%H%M%S")

    # If the user does not select a file, the browser submits an
    # empty file without a filename.
    if file.filename == "":
        flash("No selected file")
        return "Error. File did not attached", 400
    if file and allowed_file(file.filename):
        ext = pathlib.Path(file.filename).suffix
        filename = secure_filename(user + "--" + str(timestr) + ext)
        if ext == ".csv":
            file.save(os.path.join(FOLDER_USERS_CSV, filename))
            return "Ok. File uploaded: " + str(filename), 201
        elif ext == ".ckpt":
            file.save(os.path.join(FOLDER_USERS_CKPT, filename))
            return "Ok. File uploaded: " + str(filename), 201
        else:
            return "Error. File do not allowed", 400
    return "Error. File does not allowed (csv or ckpt)", 400


# Retrieve the most aggregated model
@app.get("/model", tags=[model_tag])
def get_model():
    """Get the aggregated model
    to get the aggregated model composed by the ckpt of the users
    """
    most_recent_file = None
    most_recent_time = 0

    # iterate over the files in the directory using os.scandir
    for entry in os.scandir(FOLDER_AGG_MODEL):
        if entry.is_file():
            # get the modification time of the file using entry.stat().st_mtime_ns
            mod_time = entry.stat().st_mtime_ns
            if mod_time > most_recent_time:
                # update the most recent file and its modification time
                most_recent_file = entry.name
                most_recent_time = mod_time
    return send_from_directory(FOLDER_AGG_MODEL, most_recent_file), 201


# Retrieve the aggregated model filename
@app.get("/model-filename", tags=[model_tag])
def get_model_filename():
    """Get the aggregated model filename
    """
    most_recent_file = None
    most_recent_time = 0

    # iterate over the files in the directory using os.scandir
    for entry in os.scandir(FOLDER_AGG_MODEL):
        if entry.is_file():
            # get the modification time of the file using entry.stat().st_mtime_ns
            mod_time = entry.stat().st_mtime_ns
            if mod_time > most_recent_time:
                # update the most recent file and its modification time
                most_recent_file = entry.name
                most_recent_time = mod_time
    return most_recent_file, 201


def allowed_file(filename):
    return "." in filename and filename.rsplit(".", 1)[1].lower() in ALLOWED_EXTENSIONS


if __name__ == "__main__":
    app.run(debug=True)

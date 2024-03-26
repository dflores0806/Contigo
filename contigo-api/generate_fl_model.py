import numpy as np
import tensorflow as tf
import os

# Consts (data paths)
FOLDER_USERS_CKPT = "data/users_ckpt/"
FOLDER_MODEL = "data/base_model/"
FOLDER_AGG_MODEL = "data/aggregated_ckpt/"

# Create a list to store the values of the weights of each checkpoint
checkpoint_weights_list = []

# Path to original TensorFlow Lite model
original_tflite_model_path = FOLDER_MODEL + "model_cloud.tflite"

# Read the contents of the TensorFlow Lite model from file
with open(original_tflite_model_path, "rb") as f:
    tflite_model_content = f.read()

# Create an interpreter for the TensorFlow Lite model
interpreter = tf.lite.Interpreter(model_content=tflite_model_content)
interpreter.allocate_tensors()

# Get the indexes of the weight tensors in the TensorFlow Lite model
weight_indices = [
    i["index"] for i in interpreter.get_tensor_details() if "weight" in i["name"]
]

# Load and accumulate the values of the weights of each checkpoint
checkpoint_paths = os.listdir(FOLDER_USERS_CKPT)
for checkpoint_path in checkpoint_paths:
    # print(" - CKPT=" + checkpoint_path)
    checkpoint_reader = tf.train.load_checkpoint(FOLDER_USERS_CKPT + checkpoint_path)
    checkpoint_weights = [
        checkpoint_reader.get_tensor(weight_name)
        for weight_name in checkpoint_reader.get_variable_to_shape_map()
    ]
    # print(checkpoint_path)
    # print(checkpoint_weights)
    checkpoint_weights_list.append(checkpoint_weights)


# Calculate the average of weight values
averaged_weights = [
    np.mean(weight_list, axis=0) for weight_list in zip(*checkpoint_weights_list)
]

# Modify the weight tensors in the TensorFlow Lite model with the averaged values
for i, index in enumerate(weight_indices):
    interpreter.tensor(index)()[...] = averaged_weights[i]

# Set version number
version = 0  # Default version if folder is empty
aggregated_models_paths = os.listdir(FOLDER_AGG_MODEL)
for aggregated_model_path in aggregated_models_paths:
    version = int(aggregated_model_path.split("_")[1].split(".")[0])
    # print(" - AGGMODEL=" + aggregated_model_path + ", " + str(version))
version += 1

# Save model
filename = FOLDER_AGG_MODEL + "checkpoint-global_" + str(version) + ".ckpt"
print(" - Model saved in=" + filename)
interpreter.get_signature_runner("save")(
    checkpoint_path=np.array(
        filename,
        dtype=np.string_,
    )
)

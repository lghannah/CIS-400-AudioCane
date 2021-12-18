from java import jclass
from azure.cognitiveservices.vision.computervision import ComputerVisionClient
from azure.cognitiveservices.vision.computervision.models import OperationStatusCodes
from azure.cognitiveservices.vision.computervision.models import VisualFeatureTypes
from msrest.authentication import CognitiveServicesCredentials
from array import array
import os
from PIL import Image
import sys
import time
def greet(name):
    print("--- hello,%s ---" % name)

def add(a,b):
    return a + b

def sub(count,a=0,b=0,c=0):
    return count - a - b -c

def get_list(a,b,c,d):
    return [a,b,c,d]

def print_list(data):
    print(type(data))
    for i in range(data.size()):
        print(data.get(i))

def get_java_bean():
    JavaBean = jclass("com.example.groupproject;")
    jb = JavaBean("python")
    jb.setData("json")
    jb.setData("xml")
    jb.setData("xhtml")
    return jb

def run(path):
    subscription_key = "ab014905ce9b426f95d8d06dfbd86d4c"
    endpoint = "https://400project.cognitiveservices.azure.com/"
    print("**************************"+subscription_key)
    computervision_client = ComputerVisionClient(endpoint, CognitiveServicesCredentials(subscription_key))
    images_folder = path
    print("_______________________________"+images_folder)

    remote_image_url = "https://raw.githubusercontent.com/Azure-Samples/cognitive-services-sample-data-files/master/ComputerVision/Images/landmark.jpg"
    print("===== Describe an Image - local =====")
    local_image_path = os.path.join (images_folder, "faces.jpg")
    local_image = open(local_image_path, "rb")

    # Call API
    description_result = computervision_client.describe_image_in_stream(local_image)

    # Get the response
    print("Description of local image: ")
    if (len(description_result.captions) == 0):
        print("No description detected.")
    else:
        for caption in description_result.captions:
            print("'{}' with confidence {:.2f}%".format(caption.text, caption.confidence * 100))
    print()

#     print("===== Describe an image - remote =====")
#     # Call API
#     description_results = computervision_client.describe_image(remote_image_url )
#
#     # Get the captions (descriptions) from the response, with confidence level
#     print("Description of remote image: ")
#     if (len(description_results.captions) == 0):
#         print("No description detected.")
#     else:
#         for caption in description_results.captions:
#             print("'{}' with confidence {:.2f}%".format(caption.text, caption.confidence * 100))
#     # </snippet_describe>
#
#     print()

    return description_result.captions[0].text
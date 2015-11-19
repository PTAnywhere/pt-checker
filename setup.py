"""
Created on November 19, 2015
@author: Aitor Gómez Goiri <aitor.gomez-goiri@open.ac.uk>
To install/reinstall/uninstall the project and its dependencies using pip:
     pip install ./
     pip install ./ --upgrade
     pip uninstall ptchecker
"""
from setuptools import setup  # , find_packages

setup(name="ptchecker",
      version="0.1",
      description="Python library (and Java dependency) to check that Packet Tracer is running on a given address and port.",
      author="Aitor Gomez-Goiri",
      author_email="aitor.gomez-goiri@open.ac.uk",
      maintainer="Aitor Gomez-Goiri",
      maintainer_email="aitor.gomez-goiri@open.ac.uk",
      url="https://github.com/PTAnywhere/pt-checker",
      # license = "http://www.apache.org/licenses/LICENSE-2.0",
      platforms=["any"],
      packages=["ptchecker"],
)

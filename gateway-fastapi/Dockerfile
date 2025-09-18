FROM python:3.11-slim
WORKDIR /app
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
EXPOSE 8000
# Generate gRPC Python stubs at runtime before starting the server
CMD ["bash", "-lc", "python generate_stubs.py && uvicorn main:app --host 0.0.0.0 --port 8000"]

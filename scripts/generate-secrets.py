import os
import secrets
import string
from pathlib import Path

def generate_random_string(length=32):
    """Generate a random string of fixed length"""
    alphabet = string.ascii_letters + string.digits
    return ''.join(secrets.choice(alphabet) for _ in range(length))

def main():
    # Generate secure random values
    jwt_secret = secrets.token_urlsafe(48)
    mysql_root_password = generate_random_string(32)
    mysql_user_password = generate_random_string(32)
    
    # Print values to console
    print("# Copy these values to your .env file:")
    print(f"JWT_SECRET={jwt_secret}")
    print(f"MYSQL_ROOT_PASSWORD={mysql_root_password}")
    print(f"MYSQL_PASSWORD={mysql_user_password}")
    
    # Paths
    base_dir = Path(__file__).parent.parent
    env_example = base_dir / ".env.example"
    env_file = base_dir / ".env"
    
    # Create .env file if it doesn't exist
    if not env_file.exists():
        if env_example.exists():
            # Read the example file
            with open(env_example, 'r') as f:
                content = f.read()
            
            # Replace placeholder values
            content = content.replace('your_mysql_root_password', mysql_root_password)
            content = content.replace('your_mysql_password', mysql_user_password)
            content = content.replace('generate_a_secure_secret_here', jwt_secret)
            
            # Write the new .env file
            with open(env_file, 'w') as f:
                f.write(content)
            
            print("\nCreated .env file with generated secrets")
        else:
            print("\nError: .env.example not found. Please create it first.")
    else:
        print("\n.env file already exists. Please update it with the values above.")

if __name__ == "__main__":
    main()

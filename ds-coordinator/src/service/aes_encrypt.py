import base64
from cryptography.hazmat.primitives import padding
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
from config import settings

def decrypt_aes(data):
    key = settings.aes_key
    key = key.encode('utf-8')
    data = base64.b64decode(data.encode('utf-8'))

    iv = key[:16]  # Use the first 16 bytes of the key as IV

    cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())
    decryptor = cipher.decryptor()
    padded_data = decryptor.update(data) + decryptor.finalize()

    # Unpad the decrypted data
    unpadder = padding.PKCS7(algorithms.AES.block_size).unpadder()
    decrypted_data = unpadder.update(padded_data) + unpadder.finalize()

    return decrypted_data.decode('utf-8')


def encrypt_aes(data):
    key = settings.aes_key
    data = data.encode('utf-8')
    key = key.encode('utf-8')

    # Pad the data to be multiple of block size
    padder = padding.PKCS7(algorithms.AES.block_size).padder()
    padded_data = padder.update(data) + padder.finalize()

    iv = key[:16]  # Use the first 16 bytes of the key as IV
    # iv = key  # Use the first 16 bytes of the key as IV

    cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())
    encryptor = cipher.encryptor()
    ciphertext = encryptor.update(padded_data) + encryptor.finalize()

    return base64.b64encode(ciphertext).decode('utf-8')

# Example usage

# body = {"driverClass":"com.mysql.cj.jdbc.Driver","username":"root","url":"jdbc:mysql://10.245.142.253:3307/db0","password":"root"}
# data = json.dumps(body)
# key = "O2BEeIv399qHQNhD6aGW8R8DEj4bqHXm"
# #
# encrypted_data = encrypt_aes(data, key)
# print(encrypted_data)
# # 解密


# Example usage
# encrypted_data = "your_encrypted_data"
# context = "o8M3Ko5FSf6x5vxpz8gsJPh+txBxBfFtbnsDSyn422UU+U3VFdzaMc890xhtMWW7vGdLeaZrthO/ORrEGkSsJcVrm9LZ1n/773dRvRzoU4bIuu/uCtk+BvtXgz55ueyEuiWuc/A1LlRhFBO6wPhYhNJZ9PD/aAeKLg4pvN5NZ34="
# context = "8Y3z90dAfrFG9sPpAnqfraivojTiEGcZSXWnk52JF0RCUfCCXE5sueXCwD5PUn5a2t7ZhGi7HavsiZQOjIbwUrzGSoF++CEzU0VZu3RcXWBtIuAOcwN6Nq86i5hcGGR1ItGbS9VDMrWDS/n7pW8Y0ZW+DRSqITWxEIHEYJeVr/0BrEhTJJAZKASZ9C+pd0n7CDqYIBQa3P4ctBK6+zqRe9Fxua4xOb1c9Hgw1tI+t04="
# {"driverClass":"com.mysql.cj.jdbc.Driver","url":"jdbc:mysql://10.245.142.253:3307/db0","username":"root","password":"root"} <class 'str'>
# {"name": "oracle", "driverClass": "com.mysql.cj.jdbc.Driver", "username": "root", "password": "password", "url": "jdbc:mysql://10.245.142.253:3307/db0", "remark": null} <class 'str'>
# encrypted_data = "o8M3Ko5FSf6x5vxpz8gsJPh+txBxBfFtbnsDSyn422UU+U3VFdzaMc890xhtMWW7vGdLeaZrthO/ORrEGkSsJcVrm9LZ1n/773dRvRzoU4bIuu/uCtk+BvtXgz55ueyEuiWuc/A1LlRhFBO6wPhYhNJZ9PD/aAeKLg4pvN5NZ34="
# encrypted_data = "Tux0f4CcNgPXjLHw3OB3rKvgZTiSD3RMbHmxGYWuv4eTTJXL0eJC34Sv4YmnNjzZITFxfXm75i5kJqqbB0+pgw24+NKT8c4SSdbqx8RlQGHPQJF5iH4AQ6f1rrV//CxzBu3GkMFfZPOmExJbx4Ugjim1hTI/n0Dz8wySnkVFcoUcEjJ91VZq5jN7Gky9r3va"

# decrypted_data = decrypt_aes(context)
# print(decrypted_data,type(decrypted_data))

using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using MessagingToolkit.QRCode.Codec;

namespace ALSRM.WebApp.Models
{
    public class Account
    {
        public Account()
        {
        }

        public Account(string username, string token, string role)
        {
            UserName = username;
            TokenApi = token;
            Role = role;
        }

        public string UserName { get; set; }

        public int UserId { get; set; }

        public string UserPassword { get; set; }

        public string TokenApi { get; set; }

        public string Role { get; set; }


        public static string GenerateQrCode(string value)
        {
            var qrCodecEncoder = new QRCodeEncoder();
            qrCodecEncoder.QRCodeBackgroundColor = Color.White;
            qrCodecEncoder.QRCodeForegroundColor = Color.Black;
            qrCodecEncoder.CharacterSet = "UTF-8";
            qrCodecEncoder.QRCodeEncodeMode = QRCodeEncoder.ENCODE_MODE.BYTE;
            qrCodecEncoder.QRCodeScale = 6;
            qrCodecEncoder.QRCodeVersion = 0;
            qrCodecEncoder.QRCodeErrorCorrect = QRCodeEncoder.ERROR_CORRECTION.Q;

            var imgBytes = TurnImageToByteArray(qrCodecEncoder.Encode(value));
            var imgString = Convert.ToBase64String(imgBytes);
            return $"data:image/Bmp;base64,{imgString}";
        }

        private static byte[] TurnImageToByteArray(Image img)
        {
            var ms = new MemoryStream();
            img.Save(ms, ImageFormat.Bmp);
            return ms.ToArray();
        }

        public static string GenerateQrCodeString(int id, string mac)
        {
            return "{Id=" + id + ";Mac_Bitalino=" + mac + "}";
        }
    }
}
using ALSRM.Domain.Models;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace ALSRM.Domain.Contracts.Services
{
    public interface IPointService : IDisposable
    {
        IQueryable<Point> Get();
        IQueryable<Point> Get(int examId, int stepNum);
        IQueryable<Point> Get(int examId, int stepNum, decimal x);
        Task<Point> RegisterAsync(int examId, int stepNum, decimal x, decimal y);
        Task<int> ChangeInformationAsync(int examId, int stepNum, decimal x, decimal y);
        Task<Point> FindAsync(int examId, int stepNum, decimal x);
        Task<int> SaveRepositoryAsync();
    }
}
